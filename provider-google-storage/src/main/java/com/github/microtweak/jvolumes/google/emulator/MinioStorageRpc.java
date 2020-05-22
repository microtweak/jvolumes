package com.github.microtweak.jvolumes.google.emulator;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.IOUtils;
import com.google.api.services.storage.model.*;
import com.google.cloud.Tuple;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.spi.v1.RpcBatch;
import com.google.cloud.storage.spi.v1.StorageRpc;
import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinioStorageRpc implements StorageRpc {

    private MinioClient client;

    MinioStorageRpc(StorageOptions opts) {
        final MinioCredentials credentials = (MinioCredentials) opts.getCredentials();

        try {
            this.client = new MinioClient(opts.getHost(), credentials.getAccessKey(), credentials.getSecretKey());
        } catch (InvalidEndpointException | InvalidPortException e) {
            ExceptionUtils.rethrow(e);
        }
    }

    @Override
    public Bucket create(Bucket bucket, Map<Option, ?> options) {
        return doRetuningAction(() -> {
            client.makeBucket(bucket.getName());
            return bucket;
        });
    }

    @Override
    public StorageObject create(StorageObject object, InputStream content, Map<Option, ?> options) {
        return doRetuningAction(() -> {
            final PutObjectOptions putOpts = new PutObjectOptions(content.available(), -1);

            Optional.ofNullable( object.getContentType() ).filter(StringUtils::isNotBlank).ifPresent(putOpts::setContentType);

            client.putObject(object.getBucket(), object.getName(), content, putOpts);
            return object;
        });
    }

    @Override
    public Tuple<String, Iterable<Bucket>> list(Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tuple<String, Iterable<StorageObject>> list(String bucket, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bucket get(Bucket bucket, Map<Option, ?> options) {
        return doRetuningAction(() -> client.bucketExists(bucket.getName()) ? bucket : null);
    }

    @Override
    public StorageObject get(StorageObject object, Map<Option, ?> options) {
        return doRetuningAction(() -> {
            final ObjectStat stat = client.statObject(object.getBucket(), object.getName());
            final String objectUrl = client.getObjectUrl(object.getBucket(), object.getName());

            object.setContentType( stat.contentType() );
            object.setSize( BigInteger.valueOf( stat.length() ) );
            object.setEtag( stat.etag() );
            object.setSelfLink( objectUrl );

            final Date dateTimeCreated = Date.from(stat.createdTime().toInstant());
            final TimeZone timeZoneCreated = TimeZone.getTimeZone(stat.createdTime().getZone());

            object.setTimeCreated(new DateTime(dateTimeCreated, timeZoneCreated));

            return object;
        });
    }

    public URL signUrl(BlobInfo blobInfo, long duration, TimeUnit unit, SignUrlOption... options) {
        return doRetuningAction(() -> {
            final int expiration = (int) TimeUnit.SECONDS.convert(duration, unit);

            final String signedUrl = client.getPresignedObjectUrl(Method.GET, blobInfo.getBucket(), blobInfo.getName(), expiration, Collections.emptyMap());

            return new URL(signedUrl);
        });
    }

    @Override
    public Bucket patch(Bucket bucket, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageObject patch(StorageObject storageObject, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Bucket bucket, Map<Option, ?> options) {
        return doRetuningAction(() -> {
            try {
                client.removeBucket(bucket.getName());
                return false;
            } catch (ErrorResponseException e) {
                final ErrorCode code = e.errorResponse().errorCode();

                if (code == ErrorCode.NO_SUCH_KEY) {
                    return false;
                }

                throw e;
            }
        });
    }

    @Override
    public boolean delete(StorageObject object, Map<Option, ?> options) {
        return doRetuningAction(() -> {
            try {
                client.removeObject(object.getBucket(), object.getName());
                return true;
            } catch (ErrorResponseException e) {
                final ErrorCode code = e.errorResponse().errorCode();

                if (code == ErrorCode.NO_SUCH_KEY) {
                    return false;
                }

                throw e;
            }
        });
    }

    @Override
    public RpcBatch createBatch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageObject compose(Iterable<StorageObject> sources, StorageObject target, Map<Option, ?> targetOptions) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] load(StorageObject object, Map<Option, ?> options) {
        return doRetuningAction(() -> {
            final InputStream input = client.getObject(object.getBucket(), object.getName());
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            IOUtils.copy(input, output, true);

            return output.toByteArray();
        });
    }

    @Override
    public Tuple<String, byte[]> read(StorageObject from, Map<Option, ?> options, long position, int bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long read(StorageObject from, Map<Option, ?> options, long position, OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String open(StorageObject object, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String open(String signedURL) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(String uploadId, byte[] toWrite, int toWriteOffset, long destOffset, int length, boolean last) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RewriteResponse openRewrite(RewriteRequest rewriteRequest) {
        final StorageObject source = get(rewriteRequest.source, Collections.emptyMap());
        final StorageObject target = rewriteRequest.target;

        final long blobSize = source.getSize().longValue();

        return doRetuningAction(() -> {
            client.copyObject(target.getBucket(), target.getName(), null, null, source.getBucket(), source.getName(), null, null);
            return new RewriteResponse(rewriteRequest, rewriteRequest.target, blobSize, true, "", blobSize);
        });
    }

    @Override
    public RewriteResponse continueRewrite(RewriteResponse previousResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BucketAccessControl getAcl(String bucket, String entity, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteAcl(String bucket, String entity, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BucketAccessControl createAcl(BucketAccessControl acl, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BucketAccessControl patchAcl(BucketAccessControl acl, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BucketAccessControl> listAcls(String bucket, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectAccessControl getDefaultAcl(String bucket, String entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteDefaultAcl(String bucket, String entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectAccessControl createDefaultAcl(ObjectAccessControl acl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectAccessControl patchDefaultAcl(ObjectAccessControl acl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ObjectAccessControl> listDefaultAcls(String bucket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectAccessControl getAcl(String bucket, String object, Long generation, String entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteAcl(String bucket, String object, Long generation, String entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectAccessControl createAcl(ObjectAccessControl acl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectAccessControl patchAcl(ObjectAccessControl acl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ObjectAccessControl> listAcls(String bucket, String object, Long generation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HmacKey createHmacKey(String serviceAccountEmail, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tuple<String, Iterable<HmacKeyMetadata>> listHmacKeys(Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HmacKeyMetadata updateHmacKey(HmacKeyMetadata hmacKeyMetadata, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HmacKeyMetadata getHmacKey(String accessId, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteHmacKey(HmacKeyMetadata hmacKeyMetadata, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Policy getIamPolicy(String bucket, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Policy setIamPolicy(String bucket, Policy policy, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestIamPermissionsResponse testIamPermissions(String bucket, List<String> permissions, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteNotification(String bucket, String notification) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Notification> listNotifications(String bucket) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Notification createNotification(String bucket, Notification notification) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bucket lockRetentionPolicy(Bucket bucket, Map<Option, ?> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServiceAccount getServiceAccount(String projectId) {
        throw new UnsupportedOperationException();
    }

    private <R> R doRetuningAction(MinioSupplier<R> supplier) {
        try {
            return supplier.get();
        } catch (InvalidKeyException | IllegalArgumentException | NoSuchAlgorithmException | MinioException | IOException e) {
            return ExceptionUtils.rethrow(e);
        }
    }

    interface MinioSupplier<R> {

        R get() throws MinioException, InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IOException;

    }

}
package com.github.vipul.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        System.out.println("Received create blog request");
        Blog blog = request.getBlog();
        Document doc = new Document("author_id", blog.getAuthorId())
                .append("title",blog.getTitle())
                .append("content", blog.getContent());

        System.out.println("Inserting Blog...");
        // we insert(create) the document in MongoDB
        collection.insertOne(doc);
        // Retrieve mongodb generated id
        String id = doc.getObjectId("_id").toString();
        System.out.println("Inserted Blog ID..."+id);
        CreateBlogResponse response=   CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("Received read blog request");
        String blogId = request.getBlogId();

        System.out.println("Finding blog..."+blogId);
        Document result=null;
        try{
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();

        }catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Blog with corresponding id not found")
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }
        if(result == null){
            // We don't have a match
            System.out.println("Blog not found...");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Blog with corresponding id not found")
                            .asRuntimeException()
            );
        }else{
            System.out.println("Blog found...sending response");
            Blog blog = documentToBlog(result);
            ReadBlogResponse response=   ReadBlogResponse.newBuilder()
                    .setBlog(blog)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("Received update blog request");

        Blog blog = request.getBlog();
        String blogId = blog.getId();

        System.out.println("searching blog so we can update it..."+blogId);
        Document result=null;
        try{
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();

        }catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Blog with corresponding id not found")
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }
        if(result == null){
            // We don't have a match
            System.out.println("Blog not found...");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Blog with corresponding id not found")
                            .asRuntimeException()
            );
        }else{
            System.out.println("Blog found...updating it");
            Document replacement = new Document("author_id", blog.getAuthorId())
                    .append("title",blog.getTitle())
                    .append("content", blog.getContent())
                    .append("_id", new ObjectId(blogId));

            System.out.println("Replcaing blog in database");
            collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

            System.out.println("sending response");
            responseObserver.onNext(
                    UpdateBlogResponse.newBuilder()
                            .setBlog(documentToBlog(replacement))
                            .build()
            );

            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
        System.out.println("Received delete blog request");
        String blogId = request.getBlogId();
        DeleteResult result=null;
        try {
                System.out.println("Deleting blog..."+blogId);
                result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
        }catch (Exception e){
            System.out.println("Blog not found..."+blogId);
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Blog with corresponding id not found")
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }
        if(result.getDeletedCount() == 0 ){
            System.out.println("Blog not found..."+blogId);
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Blog with corresponding id not found")
                            .asRuntimeException()
            );
        }else {
            System.out.println("Blog deleted..sending response");
            responseObserver.onNext(DeleteBlogResponse.newBuilder()
                    .setBlogId(blogId)
                    .build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
        System.out.println("received read list request");
        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(
                ListBlogResponse.newBuilder()
                        .setBlog(documentToBlog(document))
                        .build()
        ));
        responseObserver.onCompleted();
    }

    private Blog documentToBlog(Document document){
        System.out.println("conveting document to blog");
        return Blog.newBuilder()
                .setAuthorId(document.getString("author_id"))
                .setTitle(document.getString("title"))
                .setContent(document.getString("content"))
                .setId(document.getObjectId("_id").toString())
                .build();
    }
}

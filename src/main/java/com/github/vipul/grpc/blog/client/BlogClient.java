package com.github.vipul.grpc.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
    public static void main(String[] args) {
        System.out.println("Hello from Blog client");
        BlogClient main = new BlogClient();
        main.run();
    }
    private void run(){
        System.out.println("Opening up channel");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051 )
                .usePlaintext()   // To avoid SSL Error (io.grpc.netty.shaded.io.netty.handler.ssl.NotSslRecordException: not an SSL/TLS record:)
                .build();
        String blogId = createBlog(channel);
        readBlog(channel, blogId);
        updateBlog(channel, blogId);
        deleteBlog(channel, blogId);
        listBlog(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }


    private String createBlog(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setContent("This is my first blog")
                .setTitle("First Blog")
                .setAuthorId("Vipul")
                .build();

        CreateBlogResponse createResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );

        System.out.println("Received create blog response");
        System.out.println(createResponse.toString());
        return createResponse.getBlog().getId();
    }

    private void readBlog(ManagedChannel channel, String blogId){
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        System.out.println("Reading blog...");
        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setBlogId(blogId)
                .build());

        System.out.println("Received blog response");
        System.out.println(readBlogResponse.toString());

        /*System.out.println("Reading blog with fake id...");
        ReadBlogResponse readBlogResponseNotFound = blogClinet.readBlog(ReadBlogRequest.newBuilder()
                .setBlogId("5e7ae6ad3631350e0bd2ddb9")
                .build());*/

    }

    private void updateBlog(ManagedChannel channel, String blogId){

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        System.out.println("Update blog request started");

        Blog blog = Blog.newBuilder()
                .setId(blogId)
                .setContent("This is my first updated blog")
                .setTitle("First Updated Blog")
                .setAuthorId("Changed author")
                .build();
        UpdateBlogResponse updateBlogResponse = blogClient.updateBlog(UpdateBlogRequest.newBuilder()
                .setBlog(blog)
                .build());
        System.out.println("Updated blog response received..");
        System.out.println(updateBlogResponse.toString());

    }

    private void deleteBlog(ManagedChannel channel, String blogId){

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        System.out.println("Delete blog request started");

        DeleteBlogResponse deleteBlogResponse = blogClient.deleteBlog(DeleteBlogRequest.newBuilder()
                .setBlogId(blogId)
                .build());
        System.out.println("Delete blog response received..");
        System.out.println(deleteBlogResponse.toString());

    }


    private void listBlog(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        blogClient.listBlog(ListBlogRequest.newBuilder().build()).forEachRemaining(
                listBlogResponse -> System.out.println(listBlogResponse.getBlog().toString())
        );

    }

}

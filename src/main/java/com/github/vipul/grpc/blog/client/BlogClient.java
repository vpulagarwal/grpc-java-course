package com.github.vipul.grpc.blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
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
        createBlog(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void createBlog(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub blogClinet = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setContent("This is my first blog")
                .setTitle("First Blog")
                .setAuthorId("Vipul")
                .build();

        CreateBlogResponse createResponse = blogClinet.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );

        System.out.println("Received create blog response");
        System.out.println(createResponse.toString());
    }

}

# gRPC Java

## Links
- gRPC community: https://grpc.io/community/ 
- Find extra awesome resources at this link: https://github.com/grpc-ecosystem/awesome-grpc
- Java projects and code:
    - Great collection of projects: https://github.com/saturnism/grpc-java-by-example
    - Additional plugins: https://github.com/salesforce/grpc-java-contrib
- Read the documentation https://grpc.io/docs/guides/

- GRPC Gateway : 
    - https://grpc-ecosystem.github.io/grpc-gateway/
    - https://github.com/grpc-ecosystem/grpc-gateway

## Troubleshooting
### Evans CLI

- Download : https://github.com/ktr0731/evans/releases - evans_windows_386.tar.gz

- Run Evans command in `cmd`
    - `evans -r -p 50051`
    -  `show package`
    - `show service`
    - `show message`
    - `package <packageName>`
    - `service <serviceName>`
    - `call <MethodName : eg. SumService>`
    
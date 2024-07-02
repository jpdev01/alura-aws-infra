package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecr.IRepository;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

import java.util.Map;

public class AluraServiceStack extends Stack {

    public AluraServiceStack(final Construct scope, final String id, final Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public AluraServiceStack(final Construct scope, final String id, final StackProps props, final Cluster cluster) {
        super(scope, id, props);

        // Create a load-balanced Fargate service and make it public
        ApplicationLoadBalancedFargateService service = ApplicationLoadBalancedFargateService.Builder.create(this, "AluraFargateService")
                .serviceName("alura-service-ola")
                .cluster(cluster)           // Required
                .cpu(512)                   // Default is 256
                .desiredCount(1)            // Default is 1 / number of instances.
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromEcrRepository(getRepository()))
                                .containerPort(8080)
                                .containerName("app_ola")
                                .environment(getAppEnv())
                                .logDriver(buildLogDriver())
                                .build())
                .memoryLimitMiB(1024)       // Default is 512
                .publicLoadBalancer(true)   // Default is false
                .build();

        enableAutoScaling(service);
    }

    private Map<String, String> getAppEnv() {
        return Map.of(
                "SPRING_DATASOURCE_URL", "jdbc:mysql://" + Fn.importValue("pedidos-db-endpoint") + ":3306/pedidos_ms?createDatabaseIfNotExist=true",
                "SPRING_DATASOURCE_USERNAME", "admin",
                "SPRING_DATASOURCE_PASSWORD", Fn.importValue("pedidos-db-password")
        );
    }

    private IRepository getRepository() {
        return Repository.fromRepositoryName(this, "Repository", "img-pedidos-ms");
    }

    private LogDriver buildLogDriver() {
        return LogDriver.awsLogs(AwsLogDriverProps.builder()
                .logGroup(LogGroup.Builder.create(this, "PedidosMsLogGroup")
                        .logGroupName("PedidosMsLog")
                        .removalPolicy(RemovalPolicy.DESTROY) // se apagar a stack, apaga os logs
                        .build())
                .streamPrefix("PedidosMS")
                .build());
    }

    private void enableAutoScaling(ApplicationLoadBalancedFargateService service) {
        ScalableTaskCount scalableTarget = service.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(1) // min e max de instancias
                .maxCapacity(20)
                .build());

        scalableTarget.scaleOnCpuUtilization("CpuScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(70)
                .scaleInCooldown(Duration.seconds(120)) // quanto tempo com 70% para subir a instancia
                .scaleOutCooldown(Duration.seconds(60)) // para descer a instancia
                .build());

        scalableTarget.scaleOnMemoryUtilization("MemoryScaling", MemoryUtilizationScalingProps.builder()
                .targetUtilizationPercent(70)
                .scaleInCooldown(Duration.seconds(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build());
    }
}

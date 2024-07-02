package com.myorg;

import software.amazon.awscdk.App;

public class AluraAwsInfraApp {

    public static void main(final String[] args) {
        App app = new App();

        final String vpcStackId = "Vpc";
        AluraVpcStack vpcStack = new AluraVpcStack(app, vpcStackId);

        final String clusterStackId = "Cluster";
        AluraClusterStack clusterStack = new AluraClusterStack(app, clusterStackId, vpcStack.getVpc());
        clusterStack.addDependency(vpcStack); // cluster precisa que a vpc esteja criada antes do cluster

        final String rdsStackId = "Rds";
        AluraRdsStack rdsStack = new AluraRdsStack(app, rdsStackId, vpcStack.getVpc());
        rdsStack.addDependency(clusterStack); // rds precisa que o cluster esteja criado antes do rds

        AluraServiceStack serviceStack = new AluraServiceStack(app, "Service", clusterStack.getCluster());
        serviceStack.addDependency(clusterStack);

        app.synth();
    }
}

/*
fargate - menos trabalho que o ec2 para gerenciar.
O ECS (Elastic Container Service) é uma solução da AWS que tem por objetivo facilitar o gerenciamento e disponibilização de aplicações conteinerizadas na nuvem. Esse serviço é composto por alguns recursos, como Container definition, Task definition, Service, Cluster e VPC.

 */

package com.myorg;

import software.amazon.awscdk.App;

public class AluraAwsInfraApp {

    public static void main(final String[] args) {
        App app = new App();

        AluraVpcStack vpcStack = new AluraVpcStack(app, "Vpc");
        AluraClusterStack clusterStack = new AluraClusterStack(app, "Cluster", vpcStack.getVpc());
        clusterStack.addDependency(vpcStack);

        AluraRdsStack rdsStack = new AluraRdsStack(app, "Rds", vpcStack.getVpc());
        rdsStack.addDependency(vpcStack);

        AluraServiceStack aluraServiceStack = new AluraServiceStack(app, "Service", clusterStack.getCluster());
        aluraServiceStack.addDependency(clusterStack);
        app.synth();
    }
}

/*
fargate - menos trabalho que o ec2 para gerenciar.
O ECS (Elastic Container Service) é uma solução da AWS que tem por objetivo facilitar o gerenciamento e disponibilização de aplicações conteinerizadas na nuvem. Esse serviço é composto por alguns recursos, como Container definition, Task definition, Service, Cluster e VPC.

 */

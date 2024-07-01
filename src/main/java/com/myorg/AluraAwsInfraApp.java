package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class AluraAwsInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        final String vpcStackId = "Vpc";
        new AluraVpcStack(app, vpcStackId);

        app.synth();
    }
}

/*
fargate - menos trabalho que o ec2 para gerenciar.
O ECS (Elastic Container Service) é uma solução da AWS que tem por objetivo facilitar o gerenciamento e disponibilização de aplicações conteinerizadas na nuvem. Esse serviço é composto por alguns recursos, como Container definition, Task definition, Service, Cluster e VPC.

 */

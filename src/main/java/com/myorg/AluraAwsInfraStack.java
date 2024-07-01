package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class AluraAwsInfraStack extends Stack {
    public AluraAwsInfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AluraAwsInfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
    }
}

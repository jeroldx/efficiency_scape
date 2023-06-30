package com.efficiencyScape.unitTests.ServerTest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ActivityServerTest.class, ActivityServerManagerTest.class})
public class ServerTestSuite
{
}

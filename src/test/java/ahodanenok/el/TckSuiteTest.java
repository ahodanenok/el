package ahodanenok.el;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

//@Suite
@SelectPackages("com.sun.ts.tests.el")
@IncludeClassNamePatterns(".*")
public class TckSuiteTest { }

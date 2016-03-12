package com.duitang.share;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author chuck
 * @since 11/10/15
 */
public class StringsTest {

  @Test
  public void testSplitter() {
    // no obvious difference for basic usage
    String s = "a,b,c";
    String[] sa = s.split(",");
    List<String> sl = Splitter.on(",").splitToList(s);
    Assert.assertArrayEquals(sa, sl.toArray());


    // a typical example from guava docs on github
    s = " ,a, ,b,c,";
    sa = s.split(",");  // what's the expected result?
    sl = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(s);
    assertFalse(sa.length == 3);
    assertTrue(sl.size() == 3);

    s = "   ,,a=1,b=2,c=3,d=5   ";
    Map<String, String> sm = Splitter.on(',')
        .trimResults()
        .omitEmptyStrings()
        .limit(4)  // If more than 4, IllegalArgumentException throw
        .withKeyValueSeparator('=')  // this modifier can only be the last
        .split(s);
    assertTrue(sm.size() == 4 && sm.get("d").equals("5"));
    // Anyway, there are many extra modifiers
  }

  @Test
  public void testJoiner() {
    String[] sa = {"img_url1", "img_url2", "img_url3"};
    String s = Joiner.on(";").join(Arrays.asList(sa));
    assertTrue(s.equals("img_url1;img_url2;img_url3"));


    String[] sa1 = {"blog_id1", "blog_id2", null, "blog_id4"};
    s = Joiner.on(",").skipNulls().join(sa1);
    assertTrue(s.equals("blog_id1,blog_id2,blog_id4"));
    s = Joiner.on(",").useForNull("null").join(sa1);
    assertTrue(s.equals("blog_id1,blog_id2,null,blog_id4"));

    try {  // .useForNull() and .skipNulls() are mutually exclusive
      Joiner.on(",").useForNull("null").skipNulls().join(sa1);
    } catch (UnsupportedOperationException expected) {
      expected.printStackTrace();
    }


    StringBuilder sb = new StringBuilder("http://domain/?");
    Map<String, Integer> map = ImmutableMap.of("type", 111, "ticket", 222, "token", 333);
    Joiner.on("&").withKeyValueSeparator("=").appendTo(sb, map);
    assertTrue(sb.toString().equals("http://domain/?type=111&ticket=222&token=333"));
  }

  @Test
  public void testCharMatcher() {
    assertTrue(CharMatcher.is('1').matchesAllOf("1"));
    assertTrue("1".equals(String.valueOf('1')));


    String illegalUserName = "_user\nName\tFor\r Test!";
    String legalUserName = CharMatcher.BREAKING_WHITESPACE.removeFrom(illegalUserName);
    assertTrue(legalUserName.equals("_userNameForTest!"));


    legalUserName = CharMatcher.anyOf(",;:_!").or(CharMatcher.BREAKING_WHITESPACE)
        .replaceFrom(illegalUserName, ' ');
    assertTrue(legalUserName.equals(" user Name For  Test "));


    legalUserName = CharMatcher.WHITESPACE.trimFrom(legalUserName);
    assertTrue(legalUserName.equals("user Name For  Test"));
    legalUserName = CharMatcher.WHITESPACE.collapseFrom(legalUserName, '_');
    assertTrue(legalUserName.equals("user_Name_For_Test"));


    String paramStringFromURL = "12345,67890,12334,67889,54321,09876,11223,22334";
    List<String> relatedBlogIds = Splitter.on(',')
        .trimResults().omitEmptyStrings()
        .splitToList(paramStringFromURL);
    assertTrue(relatedBlogIds.size() == 8);

//    paramStringFromURL = "12345;67890;12334;67889;54321;09876;11223;22334";

    paramStringFromURL = "12345,67890,12334,67889,54321;09876;11223；22334";

    relatedBlogIds = Splitter.on(CharMatcher.anyOf(",;；"))
        .trimResults().omitEmptyStrings()
        .splitToList(paramStringFromURL);
    assertTrue(relatedBlogIds.size() == 8);
  }

  @Test
  public void testCaseFormat() {
    String s = "a_var";
    String ss = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, s);
    assertTrue(ss.equals("aVar"));

    s = "a_method(a_var, b_var)";
    ss = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, s);
    assertTrue(ss.equals("aMethod(aVar, bVar)"));
  }
}

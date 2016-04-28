package org.easyweb4j;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.easyweb4j.annotation.Controller;
import org.easyweb4j.annotation.Request;
import org.easyweb4j.constant.RequestMethod;

@Controller(basePath = "test")
public class TestController2 {

    @Request(value="page",method=RequestMethod.GET)
	public void test(HttpServletResponse response){
		System.out.println("/root");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write("/root");
			writer.flush();
		} catch (Exception e) {
			try {
				if(writer != null){
					writer.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}

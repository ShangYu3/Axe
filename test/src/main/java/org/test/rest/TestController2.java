package org.test.rest;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Request;
import org.axe.constant.RequestMethod;

@FilterFuckOff
@Controller(basePath = "test2")
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

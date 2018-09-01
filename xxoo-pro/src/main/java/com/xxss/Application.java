package com.xxss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

import com.xxss.dao.AccountService;
import com.xxss.dao.VideoService;
import com.xxss.util.AccountCache;
import com.xxss.util.IndexCache;

@SpringBootApplication
@ServletComponentScan
public class Application extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer{

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		AccountService accountservice = ctx.getBean(AccountService.class);
		new Thread() {
			public void run () {
				AccountCache cache = new AccountCache(accountservice);
				while(true) {
					cache.updateAccountMap();
					try {
						Thread.sleep(600000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {

		container.setPort(80);
		
	}
	
	
	
}

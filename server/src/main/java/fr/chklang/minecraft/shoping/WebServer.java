package fr.chklang.minecraft.shoping;

import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import fr.chklang.minecraft.shoping.servlets.GeneralWebSocketServlet;
import fr.chklang.minecraft.shoping.servlets.RestServlet;

public class WebServer {
	
	private Server server;

	public void main(Logger pLogger) throws Exception {
		// Fix for OSGI context, else websockets sucks!
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

		// Create a basic Jetty server object that will listen on port 8080.
		// Note that if you set this to port 0
		// then a randomly available port will be assigned that you can either
		// look in the logs for the port,
		// or programmatically obtain it for use in test cases.
		server = new Server(Config.getInstance().getPort());

		// Figure out what path to serve content from
		// We look for a file, as ClassLoader.getResource() is not
		// designed to look for directories (we resolve the directory later)
		URL webRootLocation = this.getClass().getResource("/static-root/index.html");
		if (webRootLocation == null) {
			throw new RuntimeException("Unable to find resource directory");
		} else {
			pLogger.info("URL : " + webRootLocation.toString());
		}

		// Resolve file to directory
		URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$", "/"));
		pLogger.info("WebRoot is " + webRootUri);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setBaseResource(Resource.newResource(webRootUri));
		context.setWelcomeFiles(new String[] { "index.html" });

		ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
		holderPwd.setInitParameter("dirAllowed", "true");
		context.addServlet(holderPwd, "/");
		ServletHolder time = new ServletHolder("elements", RestServlet.class);
		context.addServlet(time, "/rest/*");
		ServletHolder ws = new ServletHolder("websocket", GeneralWebSocketServlet.class);
		context.addServlet(ws, "/ws/*");

		server.setHandler(context);
		// Start things up! By using the server.join() the server thread will
		// join with the current thread.
		// See
		// "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()"
		// for more details.
		server.start();
		server.join();
	}
	
	public void stop() throws Exception {
		this.server.stop();
	}
}

/*
* Fichero: ConfigFilter.java
* Autor: david.garcia
* Fecha: 07/12/2012
*/
package es.dgrc.emc.webcomponents.filters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.Manifest;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
/**
*<br><b>File Name:</b> EncodingFilter.java<br>
*
*<p><b>Description:</b> Establece el encoding para las respuestas del servidor según los parámetros
*indicados en el archivo web.xml</p>
*
*<p><b>Date:</b>07-abr-2008</p>
*
* @author david.garcia
*/
public class ConfigFilter implements Filter {
       
        /**
         * Nombre de la clase
         */
        private static final String NAME = ConfigFilter.class.getName();
        /**
         * Logger
         */
        //private static final Logger logger = Logger.getLogger(ConfigFilter.class);
        private static Logger logger;
        /**
         * params (tipo: ArrayList<String>)
         */
        private ArrayList params = null;
        /**
         * values (tipo: ArrayList<String>)
         */
        private ArrayList values = null;
       
       
        /* (non-Javadoc)
         * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
         */
        @Override
		public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException, ServletException {
                HttpServletRequest wrapperRequest = (HttpServletRequest) request;
                HttpServletResponse wrapperResponse = (HttpServletResponse)response;
                if (wrapperRequest.getRequestURI().indexOf("/errors/") != -1) {
                        chain.doFilter(request, response);
                }
                else {
	                //establecer parámetros
	                //cabeceras y encoding
	                for(int i=0; i<params.size(); i++){
	                        if(((String)params.get(i)).equalsIgnoreCase("encoding")){
	                                wrapperRequest.setCharacterEncoding((String)values.get(i));
	                                //logger.debug("encoding: " + values.get(i));
	                        }
	                        else{
	                                wrapperResponse.addHeader((String)params.get(i), (String)values.get(i));
	                                //logger.debug("cabecera: " + params.get(i) + " | valor: " + values.get(i));
	                        }
	                }
	                chain.doFilter(request, response);
                }
        }
       
        /* (non-Javadoc)
         * @see javax.servlet.Filter#destroy()
         */
        @Override
		public void destroy() {
                logger.info("[ConfigFilter] - destroy filter");
        }
       
        /* (non-Javadoc)
         * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
         */
        @Override
		public void init(FilterConfig config) throws ServletException {
    		logger = Logger.getLogger(ConfigFilter.class);
    		
            params = new ArrayList();
            values = new ArrayList();
            Enumeration allParameters = config.getInitParameterNames();
           
            while(allParameters.hasMoreElements()){
                    String item = (String) allParameters.nextElement();
                    String itemValue = config.getInitParameter(item);
                    params.add(item);
                    values.add(itemValue);
                   
                    logger.info("[ConfigFilter] - init parameter: " + item + " | value: " + itemValue);
            }
            
    		// implementation version
    		String version = "";
    		try{
    			ServletContext ctx = config.getServletContext();
    			InputStream inputStream = ctx.getResourceAsStream("/META-INF/MANIFEST.MF");
    			Manifest manifest = new Manifest(inputStream);
    			version = manifest.getMainAttributes().getValue("Implementation-Version");
    			
    			//establcer implementation-version en contexto aplicación
    			ctx.setAttribute("Implementation-Version", version);
    			logger.info("[ConfigFilter] - init. Implementation-Version: " + version);
    		}
    		catch (Exception e){
    			logger.error("[ConfigFilter] - init. ERROR - Can't load attribute Implementation-Version from /META-INF/MANIFEST.MF file.");
    		}
    		
            logger.info("[ConfigFilter] - Init " + ConfigFilter.class.getName());
        }
       
} 
package de.unihamburg.zbh.fishoracle.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.rpc.ConfigService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {

	private static final long	serialVersionUID	= 1L;

	public FoUser getSessionUserObject(){
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		return user;
	}
	
	@Override
	public void add(FoConfigData foConf) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser u = getSessionUserObject();
		
		foConf.setUserId(u.getId());
		
		db.addConfig(foConf);
	}

	@Override
	public FoConfigData fetch(int configId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getConfigForId(configId);
	}

	@Override
	public FoConfigData[] fetchForUser(int userId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getConfigForUserId(userId);
	}
	
	@Override
	public void delete(int configId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeConfig(configId);
	}
}

/*
 * Copyright (C) 2011 Samuli Penttilä <samuli.penttila@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fi.capeismi.fish.uistelupaivakirja.web.controller;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.capeismi.fish.uistelupaivakirja.web.dao.Collection;
import fi.capeismi.fish.uistelupaivakirja.web.dao.User;
import fi.capeismi.fish.uistelupaivakirja.web.model.ContainsMap;
import fi.capeismi.fish.uistelupaivakirja.web.model.PublicModel;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@Controller
@RequestMapping("/api")
public class RestfulController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private PublicModel publicModel;

	private EntityManager em = Persistence.createEntityManagerFactory("uisteluweb").createEntityManager();

	private static final String RESPONSE_EXCEPTION = "TrollingException";
	private static final String RESPONSE_TRANSACTIONTICKET = "TransactionTicket";
	private static final String RESPONSE_RESPONSE = "TrollingResponse";

	@RequestMapping(value = "/{collection}", method = RequestMethod.GET)
	@ResponseBody
	public Collection getItems(@PathVariable String collection) {
		RestfulModel model = loginService.getModel();
		return model.getTrollingObjects(collection);
	}

	@RequestMapping(value = "/views/{view}", method = RequestMethod.GET)
	@ResponseBody
	public DOMSource getSome(@PathVariable String view)
			throws SQLException, ParserConfigurationException, FactoryConfigurationError, JAXBException {
		ResultSet res = getSQLWithUser(view, "limit 12");
		ResultSetMetaData meta = res.getMetaData();

		return new DOMSource(Transformers.resultSetToXml(view, res, meta));
	}

	@RequestMapping(value = "/views/fishmap", method = RequestMethod.GET)
	@ResponseBody
	public DOMSource getWaypoints()
			throws SQLException, ParserConfigurationException, FactoryConfigurationError, JAXBException {
		return mapContainerToDomSource("", new WayPoints());
	}

	@RequestMapping(value = "/views/spinneritem", method = RequestMethod.GET)
	@ResponseBody
	public DOMSource getSpinnerItem()
			throws SQLException, ParserConfigurationException, FactoryConfigurationError, JAXBException {
		return mapContainerToDomSource("", new SpinnerItems());
	}

	private DOMSource mapContainerToDomSource(String wheres, ContainsMap fillable) throws SQLException, JAXBException,
			PropertyException, ParserConfigurationException, FactoryConfigurationError {
		ResultSet res = getSQLWithUser(fillable.getName(), wheres);
		Transformers.resultSetToMapContainer(res, res.getMetaData(), fillable);

		return Transformers.objectToDomSource(fillable);
	}

	private ResultSet getSQLWithUser(String view, String wheres) throws SQLException {
		User user = em.createQuery("from User where username = :luser", User.class)
				.setParameter("luser", loginService.getUserName()).getSingleResult();

		return Transformers
				.sqlToResultSet("select * from " + view + "_view where user_id = " + user.getId() + " " + wheres);
	}

	@RequestMapping(value = "/{doctype}", method = RequestMethod.POST)
	@ResponseBody
	public DOMSource postItems(@RequestBody Collection content, @PathVariable String doctype) {
		RestfulModel model = loginService.getModel();
		RestfulResponse response = new RestfulResponse(RESPONSE_TRANSACTIONTICKET);
		content.setType(model.getType(doctype));
		response.setContent(model.appendTrollingObjects(content).toString());
		return response.getBody();
	}

	@RequestMapping(value = "/{doctype}", method = RequestMethod.PUT)
	@ResponseBody
	public DOMSource putItems(@RequestBody Collection content, @PathVariable String doctype) {
		RestfulModel model = loginService.getModel();
		RestfulResponse response = new RestfulResponse(RESPONSE_TRANSACTIONTICKET);
		content.setType(model.getType(doctype));
		response.setContent(model.setTrollingObjects(content).toString());
		return response.getBody();
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RestfulException.class)
	@ResponseBody
	public DOMSource handleException(RestfulException ex) {
		return RestfulResponse.getResponse(RESPONSE_EXCEPTION, ex.toString());
	}

	@RequestMapping(value = "/userinfo", method = RequestMethod.GET)
	@ResponseBody
	public DOMSource userinfo()
			throws PropertyException, JAXBException, ParserConfigurationException, FactoryConfigurationError {
		RestfulModel model = loginService.getModel();

		return Transformers.objectToDomSource(model.getUser());
	}

	@RequestMapping(value = "/userinfo", method = RequestMethod.PUT, headers = "Accept=text/xml")
	@ResponseBody
	public DOMSource addUser(@RequestBody User user) {
		publicModel.addUser(user);
		return RestfulResponse.getResponse(RESPONSE_RESPONSE, "OK");
	}

	@RequestMapping(value = "/userinfo", method = RequestMethod.POST, headers = "Accept=text/xml")
	@ResponseBody
	public DOMSource setUser(@RequestBody User user) {
		RestfulModel model = loginService.getModel();
		model.setUser(user);
		return RestfulResponse.getResponse(RESPONSE_RESPONSE, "OK");
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public DOMSource logout() {
		loginService.logout();
		return RestfulResponse.getResponse(RESPONSE_RESPONSE, "OK");
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public DOMSource login(@RequestParam String j_username, @RequestParam String j_password) {
		loginService.login(j_username, j_password);
		return RestfulResponse.getResponse(RESPONSE_RESPONSE, "OK");
	}
}

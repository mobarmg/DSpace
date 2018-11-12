/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.Parameter;
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.converter.EPersonConverter;
import org.dspace.app.rest.exception.ConflictException;
import org.dspace.app.rest.exception.PatchBadRequestException;
import org.dspace.app.rest.exception.RESTAuthorizationException;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.MetadataEntryRest;
import org.dspace.app.rest.model.hateoas.EPersonResource;
import org.dspace.app.rest.model.patch.Patch;
import org.dspace.app.rest.repository.patch.EPersonPatch;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * This is the repository responsible to manage EPerson Rest object
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */

@Component(EPersonRest.CATEGORY + "." + EPersonRest.NAME)
public class EPersonRestRepository extends DSpaceRestRepository<EPersonRest, UUID> {
    EPersonService es = EPersonServiceFactory.getInstance().getEPersonService();

    @Autowired
    AuthorizeService authorizeService;

    @Autowired
    EPersonConverter converter;

    @Autowired
    EPersonPatch epersonPatch;

    @Override
    protected EPersonRest createAndReturn(Context context)
            throws AuthorizeException {
        // this need to be revisited we should receive a mock EPersonRest as input
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        EPersonRest mock = null;
        try {
            mock = mapper.readValue(req.getInputStream(), EPersonRest.class);
        } catch (IOException e1) {
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        }

        EPerson eperson = null;
        try {

        	// Check if eperson already exists
        	checkEpersonAlredyExists(context, mock);

        	// Create new eperson
            eperson = es.create(context);

            // this should be probably moved to the converter (a merge method?)
            eperson.setCanLogIn(mock.isCanLogIn());
            eperson.setRequireCertificate(mock.isRequireCertificate());
            eperson.setEmail(mock.getEmail());
            eperson.setNetid(mock.getNetid());
            if (mock.getPassword() != null) {
                es.setPassword(eperson, mock.getPassword());
            }
            es.update(context, eperson);
            if (mock.getMetadata() != null) {
                for (MetadataEntryRest mer : mock.getMetadata()) {
                    String[] metadatakey = mer.getKey().split("\\.");
                    es.addMetadata(context, eperson, metadatakey[0], metadatakey[1],
                            metadatakey.length == 3 ? metadatakey[2] : null, mer.getLanguage(), mer.getValue());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return converter.convert(eperson);
    }

    protected void checkEpersonAlredyExists(Context context, EPersonRest eperson) {
        EPerson testExists = null;
        try {
	    	// Check if it is already present an eperson with passed email
	    	testExists = es.findByEmail(context, eperson.getEmail());
	    	if (testExists != null) {
	    		String msg = "Error creating new eperson. Eperson with email '" + eperson.getEmail() + "' already exists";
	    		throw new ConflictException(msg);
	    	}
	
	    	// Check if it is already present an eperson with passed netid
	    	testExists = es.findByNetid(context, eperson.getNetid());
	    	if (testExists != null) {
	    		String msg = "Error creating new eperson. Eperson with netid '" + eperson.getNetid() + "' already exists";
	    		throw new ConflictException(msg);
	    	}
	    } catch (SQLException e) {
	        throw new RuntimeException(e.getMessage(), e);
	    }
    }

    @Override
    public void patch(Context context, HttpServletRequest request, String apiCategory, String model, UUID uuid, Patch
            patch)
            throws UnprocessableEntityException, PatchBadRequestException, SQLException, AuthorizeException {

        epersonPatch.patch(context, apiCategory, model, uuid, patch);
        // Return the updated item.
        findOne(context, uuid);
    }

    @Override
    public EPersonRest findOne(Context context, UUID id) {
        EPerson eperson = null;
        try {
            eperson = es.find(context, id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (eperson == null) {
            return null;
        }
        return converter.fromModel(eperson);
    }

    @Override
    public Page<EPersonRest> findAll(Context context, Pageable pageable) {
        List<EPerson> epersons = null;
        int total = 0;
        try {
            if (!authorizeService.isAdmin(context)) {
                throw new RESTAuthorizationException(
                        "The EPerson collection endpoint is reserved to system administrators");
            }
            total = es.countTotal(context);
            epersons = es.findAll(context, EPerson.ID, pageable.getPageSize(), pageable.getOffset());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        Page<EPersonRest> page = new PageImpl<EPerson>(epersons, pageable, total).map(converter);
        return page;
    }

    @SearchRestMethod(name = "byName")
    public Page<EPersonRest> findByName(@Parameter(value = "q", required = true) String q,
            Pageable pageable) {
        List<EPerson> epersons = null;
        int total = 0;
        try {
            Context context = obtainContext();
            epersons = es.search(context, q, pageable.getOffset(), pageable.getOffset() + pageable.getPageSize());
            total = es.searchResultCount(context, q);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        Page<EPersonRest> page = new PageImpl<EPerson>(epersons, pageable, total).map(converter);
        return page;
    }

    @SearchRestMethod(name = "byEmail")
    public EPersonRest findByEmail(@Parameter(value = "email", required = true) String email) {
        EPerson eperson = null;
        try {
            Context context = obtainContext();
            eperson = es.findByEmail(context, email);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (eperson == null) {
            return null;
        }
        return converter.fromModel(eperson);
    }

    @Override
    protected void delete(Context context, UUID id) throws AuthorizeException, RepositoryMethodNotImplementedException {
        EPerson eperson = null;
        try {
            eperson = es.find(context, id);
            List<String> constraints = es.getDeleteConstraints(context, eperson);
            if (constraints != null && constraints.size() > 0) {
                throw new UnprocessableEntityException(
                        "The eperson cannot be deleted due to the following constraints: "
                                + StringUtils.join(constraints, ", "));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            es.delete(context, eperson);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Class<EPersonRest> getDomainClass() {
        return EPersonRest.class;
    }

    @Override
    public EPersonResource wrapResource(EPersonRest eperson, String... rels) {
        return new EPersonResource(eperson, utils, rels);
    }

}
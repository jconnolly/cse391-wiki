package com.welpactually.db;

import com.welpactually.model.Wiki;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class WikiDAO extends AbstractDAO<Wiki> {
    public WikiDAO(SessionFactory factory) {
        super(factory);
    }

    public Wiki findById(Long id) {
        return get(id);
    }

    public Wiki findByTitle(String title) {
        return uniqueResult(criteria().add(Restrictions.eq("title", title)));
    }

    public Wiki createOrUpdate(Wiki wiki) {
        return persist(wiki);
    }

    public List<Wiki> findAll() {
        return list(namedQuery("com.welpactually.model.Wiki.findAll"));
    }


}
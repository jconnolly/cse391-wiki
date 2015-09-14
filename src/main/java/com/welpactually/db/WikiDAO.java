package com.welpactually.db;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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

    public Wiki saveOrUpdate(Wiki wiki) {
        return update(wiki);
    }

    private Wiki update(Wiki wiki) {
        Wiki old = findByTitle(wiki.getTitle());
        old.setBody(wiki.getBody());
        return persist(old);
    }


    public List<Wiki> findAll() {
        return list(namedQuery("com.welpactually.model.Wiki.findAll"));
    }


}
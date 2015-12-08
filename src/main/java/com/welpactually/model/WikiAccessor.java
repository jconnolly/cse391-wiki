package com.welpactually.model;

import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.ViewQuery;

import java.util.List;

public interface WikiAccessor extends GenericAccessor<Wiki> {

    @ViewQuery("/^DEVICE/.test(meta.id)")
    public List<Wiki> findAll();

}
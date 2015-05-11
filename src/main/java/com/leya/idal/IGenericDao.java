package com.leya.idal;

import com.leya.idal.model.DAOMethod;
import com.leya.idal.model.PageModel;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface IGenericDao {

    public List<Object> executeQuery(DAOMethod daoMethod) throws DataAccessException;

    public Object executeLoad(DAOMethod daoMethod) throws DataAccessException;

    public Object executeInsert(DAOMethod daoMethod) throws DataAccessException;

    public int executeUpdate(DAOMethod daoMethod) throws DataAccessException;

    public int executeDelete(DAOMethod daoMethod) throws DataAccessException;

    public PageModel executePageQuery(DAOMethod daoMethod) throws DataAccessException;

    public Object executeCall(DAOMethod daoMethod) throws DataAccessException;
}

package com.kosku.dao;

import com.kosku.model.User;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.kosku.util.HibernateUtil;

public class UserDAO extends BaseDAO<User> {

	public User findByUsername(String username) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<User> query = session.createQuery("from User where username = :username", User.class);
			query.setParameter("username", username);
			return query.uniqueResult();
		} catch (Exception e) {
			throw new RuntimeException("Gagal mencari user by username: " + e.getMessage(), e);
		}
	}

	public User findByEmail(String email) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<User> query = session.createQuery("from User where email = :email", User.class);
			query.setParameter("email", email);
			return query.uniqueResult();
		} catch (Exception e) {
			throw new RuntimeException("Gagal mencari user by email: " + e.getMessage(), e);
		}
	}
}
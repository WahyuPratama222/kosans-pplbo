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
			Query<User> query = session.createQuery("from User where LOWER(email) = LOWER(:email)", User.class);
			query.setParameter("email", email.trim());
			return query.uniqueResult();
		} catch (Exception e) {
			throw new RuntimeException("Gagal mencari user by email: " + e.getMessage(), e);
		}
	}

	/**
	 * Mencari user berdasarkan Email ATAU Username (Case-Insensitive)
	 */
	public User findByIdentifier(String identifier) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "from User where LOWER(email) = LOWER(:id) OR LOWER(username) = LOWER(:id)";
			Query<User> query = session.createQuery(hql, User.class);
			query.setParameter("id", identifier.trim());
			return query.uniqueResult();
		} catch (Exception e) {
			throw new RuntimeException("Gagal mencari user by identifier: " + e.getMessage(), e);
		}
	}
}
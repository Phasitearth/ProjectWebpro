/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppClockAlertJPAModelController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AppClockAlertJPAModel.Customer;
import AppClockAlertJPAModel.Orders;
import AppClockAlertJPAModel.Payment;
import AppClockAlertJPAModelController.exceptions.NonexistentEntityException;
import AppClockAlertJPAModelController.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author phasi
 */
public class PaymentJpaController implements Serializable {

    public PaymentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Payment payment) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer customerno = payment.getCustomerno();
            if (customerno != null) {
                customerno = em.getReference(customerno.getClass(), customerno.getCustomerno());
                payment.setCustomerno(customerno);
            }
            Orders orderno = payment.getOrderno();
            if (orderno != null) {
                orderno = em.getReference(orderno.getClass(), orderno.getOrderno());
                payment.setOrderno(orderno);
            }
            em.persist(payment);
            if (customerno != null) {
                customerno.getPaymentList().add(payment);
                customerno = em.merge(customerno);
            }
            if (orderno != null) {
                orderno.getPaymentList().add(payment);
                orderno = em.merge(orderno);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Payment payment) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Payment persistentPayment = em.find(Payment.class, payment.getPaymentid());
            Customer customernoOld = persistentPayment.getCustomerno();
            Customer customernoNew = payment.getCustomerno();
            Orders ordernoOld = persistentPayment.getOrderno();
            Orders ordernoNew = payment.getOrderno();
            if (customernoNew != null) {
                customernoNew = em.getReference(customernoNew.getClass(), customernoNew.getCustomerno());
                payment.setCustomerno(customernoNew);
            }
            if (ordernoNew != null) {
                ordernoNew = em.getReference(ordernoNew.getClass(), ordernoNew.getOrderno());
                payment.setOrderno(ordernoNew);
            }
            payment = em.merge(payment);
            if (customernoOld != null && !customernoOld.equals(customernoNew)) {
                customernoOld.getPaymentList().remove(payment);
                customernoOld = em.merge(customernoOld);
            }
            if (customernoNew != null && !customernoNew.equals(customernoOld)) {
                customernoNew.getPaymentList().add(payment);
                customernoNew = em.merge(customernoNew);
            }
            if (ordernoOld != null && !ordernoOld.equals(ordernoNew)) {
                ordernoOld.getPaymentList().remove(payment);
                ordernoOld = em.merge(ordernoOld);
            }
            if (ordernoNew != null && !ordernoNew.equals(ordernoOld)) {
                ordernoNew.getPaymentList().add(payment);
                ordernoNew = em.merge(ordernoNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = payment.getPaymentid();
                if (findPayment(id) == null) {
                    throw new NonexistentEntityException("The payment with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Payment payment;
            try {
                payment = em.getReference(Payment.class, id);
                payment.getPaymentid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The payment with id " + id + " no longer exists.", enfe);
            }
            Customer customerno = payment.getCustomerno();
            if (customerno != null) {
                customerno.getPaymentList().remove(payment);
                customerno = em.merge(customerno);
            }
            Orders orderno = payment.getOrderno();
            if (orderno != null) {
                orderno.getPaymentList().remove(payment);
                orderno = em.merge(orderno);
            }
            em.remove(payment);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Payment> findPaymentEntities() {
        return findPaymentEntities(true, -1, -1);
    }

    public List<Payment> findPaymentEntities(int maxResults, int firstResult) {
        return findPaymentEntities(false, maxResults, firstResult);
    }

    private List<Payment> findPaymentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Payment.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Payment findPayment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
        }
    }

    public int getPaymentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Payment> rt = cq.from(Payment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

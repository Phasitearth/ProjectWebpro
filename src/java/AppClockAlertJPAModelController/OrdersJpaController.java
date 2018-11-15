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
import AppClockAlertJPAModel.Payment;
import java.util.ArrayList;
import java.util.List;
import AppClockAlertJPAModel.Orderdetail;
import AppClockAlertJPAModel.Orders;
import AppClockAlertJPAModelController.exceptions.IllegalOrphanException;
import AppClockAlertJPAModelController.exceptions.NonexistentEntityException;
import AppClockAlertJPAModelController.exceptions.RollbackFailureException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author phasi
 */
public class OrdersJpaController implements Serializable {

    public OrdersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Orders orders) throws RollbackFailureException, Exception {
        if (orders.getPaymentList() == null) {
            orders.setPaymentList(new ArrayList<Payment>());
        }
        if (orders.getOrderdetailList() == null) {
            orders.setOrderdetailList(new ArrayList<Orderdetail>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer customerno = orders.getCustomerno();
            if (customerno != null) {
                customerno = em.getReference(customerno.getClass(), customerno.getCustomerno());
                orders.setCustomerno(customerno);
            }
            List<Payment> attachedPaymentList = new ArrayList<Payment>();
            for (Payment paymentListPaymentToAttach : orders.getPaymentList()) {
                paymentListPaymentToAttach = em.getReference(paymentListPaymentToAttach.getClass(), paymentListPaymentToAttach.getPaymentid());
                attachedPaymentList.add(paymentListPaymentToAttach);
            }
            orders.setPaymentList(attachedPaymentList);
            List<Orderdetail> attachedOrderdetailList = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailListOrderdetailToAttach : orders.getOrderdetailList()) {
                orderdetailListOrderdetailToAttach = em.getReference(orderdetailListOrderdetailToAttach.getClass(), orderdetailListOrderdetailToAttach.getOrderdetailPK());
                attachedOrderdetailList.add(orderdetailListOrderdetailToAttach);
            }
            orders.setOrderdetailList(attachedOrderdetailList);
            em.persist(orders);
            if (customerno != null) {
                customerno.getOrdersList().add(orders);
                customerno = em.merge(customerno);
            }
            for (Payment paymentListPayment : orders.getPaymentList()) {
                Orders oldOrdernoOfPaymentListPayment = paymentListPayment.getOrderno();
                paymentListPayment.setOrderno(orders);
                paymentListPayment = em.merge(paymentListPayment);
                if (oldOrdernoOfPaymentListPayment != null) {
                    oldOrdernoOfPaymentListPayment.getPaymentList().remove(paymentListPayment);
                    oldOrdernoOfPaymentListPayment = em.merge(oldOrdernoOfPaymentListPayment);
                }
            }
            for (Orderdetail orderdetailListOrderdetail : orders.getOrderdetailList()) {
                Orders oldOrdersOfOrderdetailListOrderdetail = orderdetailListOrderdetail.getOrders();
                orderdetailListOrderdetail.setOrders(orders);
                orderdetailListOrderdetail = em.merge(orderdetailListOrderdetail);
                if (oldOrdersOfOrderdetailListOrderdetail != null) {
                    oldOrdersOfOrderdetailListOrderdetail.getOrderdetailList().remove(orderdetailListOrderdetail);
                    oldOrdersOfOrderdetailListOrderdetail = em.merge(oldOrdersOfOrderdetailListOrderdetail);
                }
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

    public void edit(Orders orders) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders persistentOrders = em.find(Orders.class, orders.getOrderno());
            Customer customernoOld = persistentOrders.getCustomerno();
            Customer customernoNew = orders.getCustomerno();
            List<Payment> paymentListOld = persistentOrders.getPaymentList();
            List<Payment> paymentListNew = orders.getPaymentList();
            List<Orderdetail> orderdetailListOld = persistentOrders.getOrderdetailList();
            List<Orderdetail> orderdetailListNew = orders.getOrderdetailList();
            List<String> illegalOrphanMessages = null;
            for (Payment paymentListOldPayment : paymentListOld) {
                if (!paymentListNew.contains(paymentListOldPayment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Payment " + paymentListOldPayment + " since its orderno field is not nullable.");
                }
            }
            for (Orderdetail orderdetailListOldOrderdetail : orderdetailListOld) {
                if (!orderdetailListNew.contains(orderdetailListOldOrderdetail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orderdetail " + orderdetailListOldOrderdetail + " since its orders field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (customernoNew != null) {
                customernoNew = em.getReference(customernoNew.getClass(), customernoNew.getCustomerno());
                orders.setCustomerno(customernoNew);
            }
            List<Payment> attachedPaymentListNew = new ArrayList<Payment>();
            for (Payment paymentListNewPaymentToAttach : paymentListNew) {
                paymentListNewPaymentToAttach = em.getReference(paymentListNewPaymentToAttach.getClass(), paymentListNewPaymentToAttach.getPaymentid());
                attachedPaymentListNew.add(paymentListNewPaymentToAttach);
            }
            paymentListNew = attachedPaymentListNew;
            orders.setPaymentList(paymentListNew);
            List<Orderdetail> attachedOrderdetailListNew = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailListNewOrderdetailToAttach : orderdetailListNew) {
                orderdetailListNewOrderdetailToAttach = em.getReference(orderdetailListNewOrderdetailToAttach.getClass(), orderdetailListNewOrderdetailToAttach.getOrderdetailPK());
                attachedOrderdetailListNew.add(orderdetailListNewOrderdetailToAttach);
            }
            orderdetailListNew = attachedOrderdetailListNew;
            orders.setOrderdetailList(orderdetailListNew);
            orders = em.merge(orders);
            if (customernoOld != null && !customernoOld.equals(customernoNew)) {
                customernoOld.getOrdersList().remove(orders);
                customernoOld = em.merge(customernoOld);
            }
            if (customernoNew != null && !customernoNew.equals(customernoOld)) {
                customernoNew.getOrdersList().add(orders);
                customernoNew = em.merge(customernoNew);
            }
            for (Payment paymentListNewPayment : paymentListNew) {
                if (!paymentListOld.contains(paymentListNewPayment)) {
                    Orders oldOrdernoOfPaymentListNewPayment = paymentListNewPayment.getOrderno();
                    paymentListNewPayment.setOrderno(orders);
                    paymentListNewPayment = em.merge(paymentListNewPayment);
                    if (oldOrdernoOfPaymentListNewPayment != null && !oldOrdernoOfPaymentListNewPayment.equals(orders)) {
                        oldOrdernoOfPaymentListNewPayment.getPaymentList().remove(paymentListNewPayment);
                        oldOrdernoOfPaymentListNewPayment = em.merge(oldOrdernoOfPaymentListNewPayment);
                    }
                }
            }
            for (Orderdetail orderdetailListNewOrderdetail : orderdetailListNew) {
                if (!orderdetailListOld.contains(orderdetailListNewOrderdetail)) {
                    Orders oldOrdersOfOrderdetailListNewOrderdetail = orderdetailListNewOrderdetail.getOrders();
                    orderdetailListNewOrderdetail.setOrders(orders);
                    orderdetailListNewOrderdetail = em.merge(orderdetailListNewOrderdetail);
                    if (oldOrdersOfOrderdetailListNewOrderdetail != null && !oldOrdersOfOrderdetailListNewOrderdetail.equals(orders)) {
                        oldOrdersOfOrderdetailListNewOrderdetail.getOrderdetailList().remove(orderdetailListNewOrderdetail);
                        oldOrdersOfOrderdetailListNewOrderdetail = em.merge(oldOrdersOfOrderdetailListNewOrderdetail);
                    }
                }
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
                Integer id = orders.getOrderno();
                if (findOrders(id) == null) {
                    throw new NonexistentEntityException("The orders with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders orders;
            try {
                orders = em.getReference(Orders.class, id);
                orders.getOrderno();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orders with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Payment> paymentListOrphanCheck = orders.getPaymentList();
            for (Payment paymentListOrphanCheckPayment : paymentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Orders (" + orders + ") cannot be destroyed since the Payment " + paymentListOrphanCheckPayment + " in its paymentList field has a non-nullable orderno field.");
            }
            List<Orderdetail> orderdetailListOrphanCheck = orders.getOrderdetailList();
            for (Orderdetail orderdetailListOrphanCheckOrderdetail : orderdetailListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Orders (" + orders + ") cannot be destroyed since the Orderdetail " + orderdetailListOrphanCheckOrderdetail + " in its orderdetailList field has a non-nullable orders field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Customer customerno = orders.getCustomerno();
            if (customerno != null) {
                customerno.getOrdersList().remove(orders);
                customerno = em.merge(customerno);
            }
            em.remove(orders);
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

    public List<Orders> findOrdersEntities() {
        return findOrdersEntities(true, -1, -1);
    }

    public List<Orders> findOrdersEntities(int maxResults, int firstResult) {
        return findOrdersEntities(false, maxResults, firstResult);
    }

    private List<Orders> findOrdersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orders.class));
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

    public Orders findOrders(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orders.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orders> rt = cq.from(Orders.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

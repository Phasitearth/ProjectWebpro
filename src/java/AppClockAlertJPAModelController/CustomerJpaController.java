/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppClockAlertJPAModelController;

import AppClockAlertJPAModel.Customer;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AppClockAlertJPAModel.Payment;
import java.util.ArrayList;
import java.util.List;
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
public class CustomerJpaController implements Serializable {

    public CustomerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Customer customer) throws RollbackFailureException, Exception {
        if (customer.getPaymentList() == null) {
            customer.setPaymentList(new ArrayList<Payment>());
        }
        if (customer.getOrdersList() == null) {
            customer.setOrdersList(new ArrayList<Orders>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Payment> attachedPaymentList = new ArrayList<Payment>();
            for (Payment paymentListPaymentToAttach : customer.getPaymentList()) {
                paymentListPaymentToAttach = em.getReference(paymentListPaymentToAttach.getClass(), paymentListPaymentToAttach.getPaymentid());
                attachedPaymentList.add(paymentListPaymentToAttach);
            }
            customer.setPaymentList(attachedPaymentList);
            List<Orders> attachedOrdersList = new ArrayList<Orders>();
            for (Orders ordersListOrdersToAttach : customer.getOrdersList()) {
                ordersListOrdersToAttach = em.getReference(ordersListOrdersToAttach.getClass(), ordersListOrdersToAttach.getOrderno());
                attachedOrdersList.add(ordersListOrdersToAttach);
            }
            customer.setOrdersList(attachedOrdersList);
            em.persist(customer);
            for (Payment paymentListPayment : customer.getPaymentList()) {
                Customer oldCustomernoOfPaymentListPayment = paymentListPayment.getCustomerno();
                paymentListPayment.setCustomerno(customer);
                paymentListPayment = em.merge(paymentListPayment);
                if (oldCustomernoOfPaymentListPayment != null) {
                    oldCustomernoOfPaymentListPayment.getPaymentList().remove(paymentListPayment);
                    oldCustomernoOfPaymentListPayment = em.merge(oldCustomernoOfPaymentListPayment);
                }
            }
            for (Orders ordersListOrders : customer.getOrdersList()) {
                Customer oldCustomernoOfOrdersListOrders = ordersListOrders.getCustomerno();
                ordersListOrders.setCustomerno(customer);
                ordersListOrders = em.merge(ordersListOrders);
                if (oldCustomernoOfOrdersListOrders != null) {
                    oldCustomernoOfOrdersListOrders.getOrdersList().remove(ordersListOrders);
                    oldCustomernoOfOrdersListOrders = em.merge(oldCustomernoOfOrdersListOrders);
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

    public void edit(Customer customer) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer persistentCustomer = em.find(Customer.class, customer.getCustomerno());
            List<Payment> paymentListOld = persistentCustomer.getPaymentList();
            List<Payment> paymentListNew = customer.getPaymentList();
            List<Orders> ordersListOld = persistentCustomer.getOrdersList();
            List<Orders> ordersListNew = customer.getOrdersList();
            List<String> illegalOrphanMessages = null;
            for (Payment paymentListOldPayment : paymentListOld) {
                if (!paymentListNew.contains(paymentListOldPayment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Payment " + paymentListOldPayment + " since its customerno field is not nullable.");
                }
            }
            for (Orders ordersListOldOrders : ordersListOld) {
                if (!ordersListNew.contains(ordersListOldOrders)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orders " + ordersListOldOrders + " since its customerno field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Payment> attachedPaymentListNew = new ArrayList<Payment>();
            for (Payment paymentListNewPaymentToAttach : paymentListNew) {
                paymentListNewPaymentToAttach = em.getReference(paymentListNewPaymentToAttach.getClass(), paymentListNewPaymentToAttach.getPaymentid());
                attachedPaymentListNew.add(paymentListNewPaymentToAttach);
            }
            paymentListNew = attachedPaymentListNew;
            customer.setPaymentList(paymentListNew);
            List<Orders> attachedOrdersListNew = new ArrayList<Orders>();
            for (Orders ordersListNewOrdersToAttach : ordersListNew) {
                ordersListNewOrdersToAttach = em.getReference(ordersListNewOrdersToAttach.getClass(), ordersListNewOrdersToAttach.getOrderno());
                attachedOrdersListNew.add(ordersListNewOrdersToAttach);
            }
            ordersListNew = attachedOrdersListNew;
            customer.setOrdersList(ordersListNew);
            customer = em.merge(customer);
            for (Payment paymentListNewPayment : paymentListNew) {
                if (!paymentListOld.contains(paymentListNewPayment)) {
                    Customer oldCustomernoOfPaymentListNewPayment = paymentListNewPayment.getCustomerno();
                    paymentListNewPayment.setCustomerno(customer);
                    paymentListNewPayment = em.merge(paymentListNewPayment);
                    if (oldCustomernoOfPaymentListNewPayment != null && !oldCustomernoOfPaymentListNewPayment.equals(customer)) {
                        oldCustomernoOfPaymentListNewPayment.getPaymentList().remove(paymentListNewPayment);
                        oldCustomernoOfPaymentListNewPayment = em.merge(oldCustomernoOfPaymentListNewPayment);
                    }
                }
            }
            for (Orders ordersListNewOrders : ordersListNew) {
                if (!ordersListOld.contains(ordersListNewOrders)) {
                    Customer oldCustomernoOfOrdersListNewOrders = ordersListNewOrders.getCustomerno();
                    ordersListNewOrders.setCustomerno(customer);
                    ordersListNewOrders = em.merge(ordersListNewOrders);
                    if (oldCustomernoOfOrdersListNewOrders != null && !oldCustomernoOfOrdersListNewOrders.equals(customer)) {
                        oldCustomernoOfOrdersListNewOrders.getOrdersList().remove(ordersListNewOrders);
                        oldCustomernoOfOrdersListNewOrders = em.merge(oldCustomernoOfOrdersListNewOrders);
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
                Integer id = customer.getCustomerno();
                if (findCustomer(id) == null) {
                    throw new NonexistentEntityException("The customer with id " + id + " no longer exists.");
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
            Customer customer;
            try {
                customer = em.getReference(Customer.class, id);
                customer.getCustomerno();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customer with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Payment> paymentListOrphanCheck = customer.getPaymentList();
            for (Payment paymentListOrphanCheckPayment : paymentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customer (" + customer + ") cannot be destroyed since the Payment " + paymentListOrphanCheckPayment + " in its paymentList field has a non-nullable customerno field.");
            }
            List<Orders> ordersListOrphanCheck = customer.getOrdersList();
            for (Orders ordersListOrphanCheckOrders : ordersListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customer (" + customer + ") cannot be destroyed since the Orders " + ordersListOrphanCheckOrders + " in its ordersList field has a non-nullable customerno field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(customer);
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

    public List<Customer> findCustomerEntities() {
        return findCustomerEntities(true, -1, -1);
    }

    public List<Customer> findCustomerEntities(int maxResults, int firstResult) {
        return findCustomerEntities(false, maxResults, firstResult);
    }

    private List<Customer> findCustomerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Customer.class));
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

    public Customer findCustomer(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }
    public Customer findUsername(String username){
        EntityManager em = getEntityManager();
        Query query = em.createNamedQuery("Customer.findByUsername");
        query.setParameter("username", username);
        try {
            return (Customer) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        finally{
            em.close();
        }
    }

    public int getCustomerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Customer> rt = cq.from(Customer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

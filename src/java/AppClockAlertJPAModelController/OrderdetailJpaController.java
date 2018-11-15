/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppClockAlertJPAModelController;

import AppClockAlertJPAModel.Orderdetail;
import AppClockAlertJPAModel.OrderdetailPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AppClockAlertJPAModel.Orders;
import AppClockAlertJPAModel.Products;
import AppClockAlertJPAModelController.exceptions.NonexistentEntityException;
import AppClockAlertJPAModelController.exceptions.PreexistingEntityException;
import AppClockAlertJPAModelController.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author phasi
 */
public class OrderdetailJpaController implements Serializable {

    public OrderdetailJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Orderdetail orderdetail) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (orderdetail.getOrderdetailPK() == null) {
            orderdetail.setOrderdetailPK(new OrderdetailPK());
        }
        orderdetail.getOrderdetailPK().setOrderno(orderdetail.getOrders().getOrderno());
        orderdetail.getOrderdetailPK().setProductcode(orderdetail.getProducts().getProductcode());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orders orders = orderdetail.getOrders();
            if (orders != null) {
                orders = em.getReference(orders.getClass(), orders.getOrderno());
                orderdetail.setOrders(orders);
            }
            Products products = orderdetail.getProducts();
            if (products != null) {
                products = em.getReference(products.getClass(), products.getProductcode());
                orderdetail.setProducts(products);
            }
            em.persist(orderdetail);
            if (orders != null) {
                orders.getOrderdetailList().add(orderdetail);
                orders = em.merge(orders);
            }
            if (products != null) {
                products.getOrderdetailList().add(orderdetail);
                products = em.merge(products);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findOrderdetail(orderdetail.getOrderdetailPK()) != null) {
                throw new PreexistingEntityException("Orderdetail " + orderdetail + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Orderdetail orderdetail) throws NonexistentEntityException, RollbackFailureException, Exception {
        orderdetail.getOrderdetailPK().setOrderno(orderdetail.getOrders().getOrderno());
        orderdetail.getOrderdetailPK().setProductcode(orderdetail.getProducts().getProductcode());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orderdetail persistentOrderdetail = em.find(Orderdetail.class, orderdetail.getOrderdetailPK());
            Orders ordersOld = persistentOrderdetail.getOrders();
            Orders ordersNew = orderdetail.getOrders();
            Products productsOld = persistentOrderdetail.getProducts();
            Products productsNew = orderdetail.getProducts();
            if (ordersNew != null) {
                ordersNew = em.getReference(ordersNew.getClass(), ordersNew.getOrderno());
                orderdetail.setOrders(ordersNew);
            }
            if (productsNew != null) {
                productsNew = em.getReference(productsNew.getClass(), productsNew.getProductcode());
                orderdetail.setProducts(productsNew);
            }
            orderdetail = em.merge(orderdetail);
            if (ordersOld != null && !ordersOld.equals(ordersNew)) {
                ordersOld.getOrderdetailList().remove(orderdetail);
                ordersOld = em.merge(ordersOld);
            }
            if (ordersNew != null && !ordersNew.equals(ordersOld)) {
                ordersNew.getOrderdetailList().add(orderdetail);
                ordersNew = em.merge(ordersNew);
            }
            if (productsOld != null && !productsOld.equals(productsNew)) {
                productsOld.getOrderdetailList().remove(orderdetail);
                productsOld = em.merge(productsOld);
            }
            if (productsNew != null && !productsNew.equals(productsOld)) {
                productsNew.getOrderdetailList().add(orderdetail);
                productsNew = em.merge(productsNew);
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
                OrderdetailPK id = orderdetail.getOrderdetailPK();
                if (findOrderdetail(id) == null) {
                    throw new NonexistentEntityException("The orderdetail with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(OrderdetailPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Orderdetail orderdetail;
            try {
                orderdetail = em.getReference(Orderdetail.class, id);
                orderdetail.getOrderdetailPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orderdetail with id " + id + " no longer exists.", enfe);
            }
            Orders orders = orderdetail.getOrders();
            if (orders != null) {
                orders.getOrderdetailList().remove(orderdetail);
                orders = em.merge(orders);
            }
            Products products = orderdetail.getProducts();
            if (products != null) {
                products.getOrderdetailList().remove(orderdetail);
                products = em.merge(products);
            }
            em.remove(orderdetail);
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

    public List<Orderdetail> findOrderdetailEntities() {
        return findOrderdetailEntities(true, -1, -1);
    }

    public List<Orderdetail> findOrderdetailEntities(int maxResults, int firstResult) {
        return findOrderdetailEntities(false, maxResults, firstResult);
    }

    private List<Orderdetail> findOrderdetailEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orderdetail.class));
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

    public Orderdetail findOrderdetail(OrderdetailPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orderdetail.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrderdetailCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orderdetail> rt = cq.from(Orderdetail.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

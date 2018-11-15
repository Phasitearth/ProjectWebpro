/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AppClockAlertJPAModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author phasi
 */
@Entity
@Table(name = "PRODUCTS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Products.findAll", query = "SELECT p FROM Products p")
    , @NamedQuery(name = "Products.findByProductcode", query = "SELECT p FROM Products p WHERE p.productcode = :productcode")
    , @NamedQuery(name = "Products.findByProductname", query = "SELECT p FROM Products p WHERE lower(p.productname) Like :productname")
    , @NamedQuery(name = "Products.findByProductprice", query = "SELECT p FROM Products p WHERE p.productprice = :productprice")
    , @NamedQuery(name = "Products.findByProductdetail", query = "SELECT p FROM Products p WHERE p.productdetail = :productdetail")
    , @NamedQuery(name = "Products.search", query = "SELECT p FROM Products p WHERE lower(p.productname) Like :productname or lower(p.productdetail) like :productdetail")})
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "PRODUCTCODE")
    private String productcode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PRODUCTNAME")
    private String productname;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRODUCTPRICE")
    private BigDecimal productprice;
    @Size(max = 600)
    @Column(name = "PRODUCTDETAIL")
    private String productdetail;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "products")
    private List<Orderdetail> orderdetailList;

    public Products() {
    }

    public Products(String productcode) {
        this.productcode = productcode;
    }

    public Products(String productcode, String productname, BigDecimal productprice) {
        this.productcode = productcode;
        this.productname = productname;
        this.productprice = productprice;
    }

    public Products(String productcode, String productname, BigDecimal productprice, String productdetail) {
        this.productcode = productcode;
        this.productname = productname;
        this.productprice = productprice;
        this.productdetail = productdetail;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public BigDecimal getProductprice() {
        return productprice;
    }

    public void setProductprice(BigDecimal productprice) {
        this.productprice = productprice;
    }

    public String getProductdetail() {
        return productdetail;
    }

    public void setProductdetail(String productdetail) {
        this.productdetail = productdetail;
    }

    @XmlTransient
    public List<Orderdetail> getOrderdetailList() {
        return orderdetailList;
    }

    public void setOrderdetailList(List<Orderdetail> orderdetailList) {
        this.orderdetailList = orderdetailList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productcode != null ? productcode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Products)) {
            return false;
        }
        Products other = (Products) object;
        if ((this.productcode == null && other.productcode != null) || (this.productcode != null && !this.productcode.equals(other.productcode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AppClockAlertJPAModel.Products[ productcode=" + productcode + " ]";
    }
    
}

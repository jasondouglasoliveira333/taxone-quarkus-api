package br.com.lkm.taxone.mapper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class OURAddress{

  @Id
  private Integer id;
  private String name;

  @OneToOne(mappedBy="OURAddress")
  private Customer customer;

  public Integer getId(){
    return id;
  }
  
  public void setId(Integer id){
    this.id = id;
  }

  public String getName(){
    return name;
  }
  
  public void setName(String name){
    this.name = name;
  }

  public Customer getCustomer(){
    return customer;
  }
  
  public void setCustomer(Customer customer){
    this.customer = customer;
  }

}
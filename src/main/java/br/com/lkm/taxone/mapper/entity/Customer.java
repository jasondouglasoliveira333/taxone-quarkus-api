package br.com.lkm.taxone.mapper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Customer{

  @Id
  private Integer id;
  private String name;

  @OneToOne
  private OURAddress OURAddress;

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

  public OURAddress getOURAddress(){
    return OURAddress;
  }
  
  public void setOURAddress(OURAddress OURAddress){
    this.OURAddress = OURAddress;
  }

}
package com.zerobase.zerobaseheritage.entity;

import com.zerobase.zerobaseheritage.datatype.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MemberEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY )
  private Long id;
  @Column(unique=true)
  private String memberId;
  private String password;
  private Role role;



}


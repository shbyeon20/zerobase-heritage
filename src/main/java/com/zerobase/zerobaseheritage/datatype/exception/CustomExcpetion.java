package com.zerobase.zerobaseheritage.datatype.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomExcpetion extends RuntimeException {
  private ErrorCode errorCode;
  private String message;
}

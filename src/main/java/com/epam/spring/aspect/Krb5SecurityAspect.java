package com.epam.spring.aspect;

import org.apache.hadoop.security.UserGroupInformation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class Krb5SecurityAspect {
  @Pointcut( "@annotation(com.epam.spring.annotation.SecurityAnnotation)" )
  public void test() {
  }

  @SuppressWarnings( { "unchecked", "ConstantConditions" } )
  @Around( "test()" )
  public CompletableFuture<Boolean> aroundTest( ProceedingJoinPoint joinPoint ) throws Exception {
    return (CompletableFuture<Boolean>) UserGroupInformation.getLoginUser()
      .doAs( (PrivilegedExceptionAction<Object>) () -> {
        try {
          return joinPoint.proceed();
        } catch ( Throwable throwable ) {
          throw (Exception) throwable;
        }
      } );
        /*return (Boolean) Subject.doAs(getKrb5Subject(), (PrivilegedExceptionAction<Object>) () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw (Exception) throwable;
            }
        });*/
  }

  //    private Subject getKrb5Subject() throws Exception {
  //        Authentication loggedAuthentication = SecurityContextHolder.getContext().getAuthentication();
  //        if (loggedAuthentication instanceof TestConfigLoadCredentials){
  //            TestConfigLoadCredentials authentication = (TestConfigLoadCredentials) loggedAuthentication;
  //            if (authentication.getKrb5Subject() != null){
  //                return authentication.getKrb5Subject();
  //            }
  //        }
  //
  //        throw new Exception("tt");
  //    }
}

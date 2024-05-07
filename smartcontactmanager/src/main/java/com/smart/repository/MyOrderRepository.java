package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Contact;
import com.smart.entity.MyOrder;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {

	

	@Query("select u from MyOrder u where u.orderId = :orderId")
	public MyOrder findByOrderId(@Param("orderId")String orderId);

	
}

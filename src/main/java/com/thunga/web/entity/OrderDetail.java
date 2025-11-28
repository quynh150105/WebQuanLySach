package com.thunga.web.entity;

import javax.persistence.*;

import com.thunga.web.model.CartDetailInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="order_detail")
@Data
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;
	private int total_cost;
	private int number;
	private String created_at;
	private String updated_at;
	
	public OrderDetail(CartDetailInfo cartDetailInfo) {
		this.book = cartDetailInfo.getBook();
		this.number = cartDetailInfo.getQuantity();
		this.total_cost = cartDetailInfo.getAmount();
	}
}

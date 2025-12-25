package com.infy.facebook_group3.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="friend_requests", uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id","receiver_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;
	
	@ManyToOne
	@JoinColumn(name="sender_id",nullable = false)
	private User sender;
	
	@ManyToOne
	@JoinColumn(name="receiver_id",nullable = false)
	private User receiver;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public enum Status{
		PENDING,ACCEPTED,DECLINED
	}
}

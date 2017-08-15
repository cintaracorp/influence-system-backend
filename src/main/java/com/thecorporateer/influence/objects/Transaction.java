package com.thecorporateer.influence.objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Zollak
 * 
 *         Entity to store transactions
 *
 */
@Getter
@Setter
@NoArgsConstructor

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Transaction extends JpaEntity {

	@NotNull
	@NotBlank
	private String timestamp;
	@NotNull
	@Min(1)
	private int amount;
	@NotNull
	@ManyToOne
	private Corporateer sender;
	@NotNull
	@ManyToOne
	private Corporateer receiver;
	@NotNull
	@NotBlank
	private String message;
	@NotNull
	@ManyToOne
	private InfluenceType type;
	@NotNull
	@ManyToOne
	private Division division;
	@NotNull
	@ManyToOne
	private Department department;

}

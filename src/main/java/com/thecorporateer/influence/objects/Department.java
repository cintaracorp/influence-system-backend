package com.thecorporateer.influence.objects;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Department extends JpaEntity {
	
	public Department(String name) {
		this.name=name;
	}

	@NotNull
	@NotBlank
	private String name;
	@OneToMany(mappedBy="department")
	private List<Division> divisions;
	@OneToMany(mappedBy = "department")
	private List<Influence> influence;

}
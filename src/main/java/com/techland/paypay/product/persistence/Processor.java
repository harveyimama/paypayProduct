package com.techland.paypay.product.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techland.paypay.product.command.UserCommand;
import com.techland.paypay.product.data.MerchantUser;
import com.techland.paypay.product.impl.ProductState;

@Component
public class Processor {
	@Autowired
	MerchantRepository merchantRepo;
	@Autowired
	UserRepository userRepo;

	public ProductState getAccountdetails(String id) {

		Merchant merchant = merchantRepo.findById(id).get();
		ProductState u = new ProductState();
		u.setAddress(merchant.getAddress());
		u.setBusinessDescription(merchant.getBusinessDescription());
		u.setCategory(merchant.getCategory());
		u.setEmail(merchant.getEmail());
		u.setId(merchant.getId());
		u.setName(merchant.getName());
		u.setPhone(merchant.getPhone());
		u.setRCCNumber(merchant.getRCCNumber());

		List<User> users = userRepo.findAllByMerchant(merchant);

		u.setUsers(getUserCommand(users));

		return u;
	}

	private List<MerchantUser> getUserCommand(List<User> users) {
	
		List<MerchantUser> listUser = new ArrayList<MerchantUser>();
		for (User usr : users) {
			MerchantUser umerch = new MerchantUser();
			umerch.setEmail(usr.getEmail());
			umerch.setFullname(usr.getFullname());
			umerch.setId(usr.getId());
			umerch.setRole(usr.getRole());
			listUser.add(umerch);

		}
		return listUser;

	}

}

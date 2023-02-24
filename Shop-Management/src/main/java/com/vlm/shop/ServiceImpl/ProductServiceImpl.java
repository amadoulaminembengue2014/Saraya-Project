package com.vlm.shop.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vlm.shop.Constents.ShopConstants;
import com.vlm.shop.Dao.ProductDao;
import com.vlm.shop.Entity.Category;
import com.vlm.shop.Entity.Product;
import com.vlm.shop.Jwt.JwtFilter;
import com.vlm.shop.Service.ProductService;
import com.vlm.shop.Utils.ShopUtils;
import com.vlm.shop.Wrapper.ProductWrapper;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (validateProductMap(requestMap, false)) {
					productDao.save(getProductFromMap(requestMap, false));
					return ShopUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
				}
				return ShopUtils.getResponseEntity(ShopConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			} else 
				return ShopUtils.getResponseEntity(ShopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ShopUtils.getResponseEntity(ShopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
		if (requestMap.containsKey("name")) {
			if (requestMap.containsKey("id") && validateId) {
				return true;
			} else if (!validateId) {
				return true;
			}
		}
		return false;
	}
	
	
	private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
		Category category = new Category();
		category.setId(Integer.parseInt(requestMap.get("categoryId")));
		
		Product product = new Product();
		if (isAdd) {
			product.setId(Integer.parseInt(requestMap.get("id")));
		} else {
			product.setStatus("true");
		}
		product.setCategory(category);
		product.setName(requestMap.get("name"));
		product.setDescription(requestMap.get("description"));
		product.setPrice(Integer.parseInt(requestMap.get("price")));
		return product;
	}

	

	@Override
	public ResponseEntity<List<ProductWrapper>> getAllProduct() {
		try {
			return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	


	@Override
	public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (validateProductMap(requestMap, true)) {
					Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
					if (!optional.isEmpty()) {
						Product product = getProductFromMap(requestMap, true);
						product.setStatus(optional.get().getStatus());
						productDao.save(product);
						return ShopUtils.getResponseEntity("Product Updated Successfully", HttpStatus.OK);
					}
					else {
						return ShopUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);
					}
				} else {
					return ShopUtils.getResponseEntity(ShopConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
				}
			} else {
				return ShopUtils.getResponseEntity(ShopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ShopUtils.getResponseEntity(ShopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	

	@Override
	public ResponseEntity<String> deleteProduct(Integer id) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<Product> optional = productDao.findById(id);
				if (!optional.isEmpty()) {
					productDao.deleteById(id);
					return ShopUtils.getResponseEntity("Product deleted Successfully", HttpStatus.OK);
				}
				return ShopUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);
			} else {
				return ShopUtils.getResponseEntity(ShopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return ShopUtils.getResponseEntity(ShopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 多个表的插入,保证一致性
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDto) {
        //菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        //此时口味数据中无dish id
        dishMapper.insert(dish);
        //获取insert语句生成的主键
        Long id = dish.getId();
        //口味表插入多条数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        if(flavors != null){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}

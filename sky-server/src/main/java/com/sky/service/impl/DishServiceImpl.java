package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;
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

        if(flavors != null&&flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page< DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //思维误区,应该分开查,最后组装,而非直接利用某个mapper查表
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //封装数据进入VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断是否能够删除 起售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否能够删除 关联套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() >0){
            //被套餐关联,无法删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

//        for (Long id : ids) {
//            //每一条数据发出两条sql,性能下降
//            //删除菜品表的菜品数据
//            dishMapper.deleteById(id);
//            //删除菜品口味表的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //优化后的sql
        //delete from dish where id in ?
        //delete from dish_flavor where dish_id in (?,?,?)
        //查数据库的操作明显减少
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //关于菜品口味信息的修改方式存在很多种情况,简单的处理思路是先删后加
        //修改菜品基本信息
        Dish dish = new Dish();
        //注意beanUtils.copyProperties的漏洞
        BeanUtils.copyProperties(dish,dishDTO);
        dishMapper.update(dish);
        //删除菜品口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //新增菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null&&flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public void startOrStop(Integer status,Long id) {
        dishMapper.startOrStop(status,id);
    }
}

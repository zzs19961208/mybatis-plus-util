package io.github.heathchen.mybatisplus.util.strategy;

import io.github.heathchen.mybatisplus.util.annotation.QueryField;
import io.github.heathchen.mybatisplus.util.enums.QueryType;
import io.github.heathchen.mybatisplus.util.utils.PageHelperUtil;
import io.github.heathchen.mybatisplus.util.utils.QueryParamThreadLocal;
import io.github.heathchen.mybatisplus.util.utils.TableUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.lang.reflect.Field;

/**
 * BETWEEN 值1 AND 值2 查询策略
 * @author HeathCHEN
 * @version 1.0
 * @since 2024/02/26
 */
public class BetweenQueryTypeStrategy implements QueryTypeStrategy {

    private static final QueryType QUERY_TYPE = QueryType.BETWEEN;

    public BetweenQueryTypeStrategy() {
        QueryTypeStrategyManager.putQueryTypeStrategyToManager(QUERY_TYPE.getCompareType(), this);
    }

    /**
     * 构造查询
     * @param queryField CustomerQuery注解
     * @param clazz 类
     * @param field 字段
     * @param queryWrapper 查询queryWrapper
     * @author HeathCHEN
     */
    @Override
    public <T> void buildQuery(QueryField queryField, Class clazz, Field field, QueryWrapper<T> queryWrapper) {

        //查询属性名对应字段名
        String tableColumnName = TableUtil.getTableColumnName(clazz,field);

        Object startValue = QueryParamThreadLocal.getValueFromObjectMap(queryField.betweenStartVal());
        Object endValue = QueryParamThreadLocal.getValueFromObjectMap(queryField.betweenEndVal());
        
        if (ObjectUtil.isNotNull(startValue)) {
            queryWrapper.ge(tableColumnName, startValue);
            QueryParamThreadLocal.removeParamFromObjectMap(queryField.betweenStartVal());
        }
        if (ObjectUtil.isNotNull(endValue)) {
            queryWrapper.le(tableColumnName, endValue);
            QueryParamThreadLocal.removeParamFromObjectMap(queryField.betweenEndVal());
        }
        //检查是否使用排序
        PageHelperUtil.checkColumnOrderOnField(queryField,clazz, field,tableColumnName);
    }
}

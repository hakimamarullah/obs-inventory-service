package com.sg.obs.config;

import lombok.NonNull;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("pageableKeyGenerator")
public class PageableKeyGenerator implements KeyGenerator {

    private static final int MAX_KEY_LENGTH = 150; // truncate very long keys

    public static String generate(Object... params) {
        StringBuilder keyBuilder = new StringBuilder();

        for (Object param : params) {
            if (param instanceof Pageable pageable) {
                keyBuilder.append(generatePageKey(pageable));
            } else {
                keyBuilder.append("_").append(param);
            }
        }

        return keyBuilder.toString();
    }

    public static String generatePageKey(Pageable pageable) {
        StringBuilder key = new StringBuilder();
        key.append("page_").append(pageable.getPageNumber())
                .append("_size_").append(pageable.getPageSize());

        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            key.append("_sort_");
            for (Sort.Order order : sort) {
                key.append(order.getProperty()).append("_")
                        .append(order.getDirection()).append("_");
            }
            key.setLength(key.length() - 1); // remove trailing underscore
        }

        return key.toString();
    }

    public static String generateHash(Object... params) {
        return "hash_" + generate(params).hashCode();
    }


    @Override
    public Object generate(@NonNull Object target, @NonNull Method method, @NonNull Object... params) {
        String key = method.getName() + "_" + generate(params);
        if (key.length() > MAX_KEY_LENGTH) {
            return method.getName() + "_" + generateHash(params);
        }
        return key;
    }
}

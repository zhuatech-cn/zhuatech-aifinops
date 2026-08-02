/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.aifinops.service;
import cn.zhuatech.aifinops.common.BusinessException; import cn.zhuatech.aifinops.model.UserAccount; import cn.zhuatech.aifinops.repository.UserRepository; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.stereotype.Service;
@Service public class CurrentUserService {private final UserRepository users;public CurrentUserService(UserRepository users){this.users=users;}public UserAccount get(){String username=SecurityContextHolder.getContext().getAuthentication().getName();return users.findByUsername(username).orElseThrow(()->new BusinessException("当前用户不存在"));}}

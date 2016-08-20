package payne.framework.pigeon.generation.objc.converter;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author Payne 646742615@qq.com
 *
 * @date 2016年8月20日 下午3:07:05
 *
 * @since 1.0.0
 */
public class NumberConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz);
	}

	@Override
	protected String convert(Class<?> clazz) {
		return "NSNumber *";
	}
}

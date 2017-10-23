package util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Sorts a map by the values of the entries.
 * 
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 * 
 */
public class MapUtil {
	/**
	 * Sorts a map by the values of the entries.
	 * 
	 * @param map
	 *          map short to
	 * @return shorted map (by entry value)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private static class DescValueComparer<K, V extends Comparable> implements Comparator {
		protected final Map<K, V> map;

		@SuppressWarnings("unchecked")
		public DescValueComparer(Map map) {
			this.map = map;
		}

		@SuppressWarnings("unchecked")
		public int compare(Object key1, Object key2) {
			V value1 = this.map.get(key1);
			V value2 = this.map.get(key2);
			int c = value1.compareTo(value2);
			if (c != 0)
				return -c;
			Integer hashCode1 = key1.hashCode();
			Integer hashCode2 = key2.hashCode();
			return -hashCode1.compareTo(hashCode2);
		}
	}

	@SuppressWarnings("rawtypes")
	private static class IncValueComparer<K, V extends Comparable> extends DescValueComparer {
		public IncValueComparer(Map map) {
			super(map);
		}

		public int compare(Object key1, Object key2) {
			return -super.compare(key1, key2);
		}
	}

	public static <K, V> SortedMap<K, V> sortMapByValue(Map<K, V> map) {
		return sortMapByValue(map, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> SortedMap<K, V> sortMapByValue(Map<K, V> map, boolean descending) {
		Comparator<?> vc = null;
		if (descending)
			vc = new DescValueComparer(map);
		else
			vc = new IncValueComparer(map);
		SortedMap sm = new TreeMap(vc);
		sm.putAll(map);
		return sm;
	}

	public static void main(String[] a) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("a", 3);
		m.put("b", 2);
		m.put("b", 4);
		m.put("d", 4);
		m.put("c", -4);
		System.out.println(sortMapByValue(m));
	}

	/***
	 * Add value to a map with numeric value
	 */
	@SuppressWarnings("unchecked")
	public static <K, T extends Number> void addToMap(Map<K, T> m, K key, T value) {
		m.put(key, m.containsKey(key) ? (T) (Double) (value.doubleValue() + m.get(key).doubleValue()) : value);
	}

	public static <K> void addToMap(Map<K, Double> m, K key, Double value) {
		m.put(key, m.containsKey(key) ? value + m.get(key) : value);
	}

	public static <K> void addToMap(Map<K, Integer> m, K key, Integer value) {
		m.put(key, m.containsKey(key) ? value + m.get(key) : value);
	}

}

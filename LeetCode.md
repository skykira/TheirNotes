# 空间效率

## 1. 利用比特位存储更多信息

`&` 运算获取 `x` 某一比特位的值

x&1 获取最后一位
x>>1 右移一位

- [289 生命游戏](https://leetcode-cn.com/problems/game-of-life/comments/)

## 2. 使用同一个数组记录当前状态

- [回溯法递归遍历，需要携带当前状态数组时，随时添加删除数组中的元素](https://leetcode-cn.com/submissions/detail/61185405/)


# 时间效率

## 1. 遍历周围数据，利用两个数组遍历

```
    private static final int[] DX = {0, 0, 1, -1, 1, 1, -1, -1};
    private static final int[] DY = {1, -1, 0, 0, 1, -1, 1, -1};
```
# 题型对应解法

- [78. 子集](https://leetcode-cn.com/problems/subsets/)

    求集合所有子集的问题，非常适合二进制位的解法。
    ```java
    public static List<List<Integer>> binaryBit(int[] nums) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        /*
            四个数的子集数目
            ==二进制数0-1111的个数
            ==10000代表的数值
        */
        for (int i = 0; i < (1 << nums.length); i++) {
            List<Integer> sub = new ArrayList<Integer>();
            //遍历每个数字，其中二进制为1的数位代表该位上的数字被选中，记录下来
            for (int j = 0; j < nums.length; j++)
                if (((i >> j) & 1) == 1) sub.add(nums[j]);
            res.add(sub);
        }
        return res;
    }
    ```
- [15. 三数之和](https://leetcode-cn.com/problems/3sum/)

    适合三指针的方法，固定一位，剩余开始剪枝遍历。

- []()水电费

# Java基础

1. int数组排序

    Arrays.sort(nums)

2. String->字符数组
    new ArrayList<>(Arrays.asList("abc".split("")))

3. int数组转为list

    List<Integer> list = Arrays.stream(nums).boxed().collect(Collectors.toList());

4. hashmap累计数值

    当name不存在时设置key的值为1，当name的值存在时，将值加1赋给name
        
    map.merge("key", 1, (oldValue, newValue) -> oldValue + newValue);

5. hashMap list为key时，值一样，则为同一个list
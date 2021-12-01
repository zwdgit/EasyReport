const format = (time, length = 19) => {
  let date;
  // 传入的是时间戳
  if (typeof time === "number" && !isNaN(time)) {
    let timeLength = time.toString().length;
    // 10位时间戳
    if (timeLength === 10) {
      date = new Date(time * 1000 + 8 * 3600 * 1000);
    } else if (timeLength === 13) {
      // 13位时间戳
      date = new Date(time + 8 * 3600 * 1000);
    } else {
      console.log("请传入10位或13位的时间戳！");
      return;
    }
  } else {
    // 传入的是中国标准时间
    date = new Date(Date.parse(time) + 8 * 3600 * 1000);
  }
  // 校验传入的时间格式
  if (!date.toJSON()) {
    console.log("传入的时间格式不正确！");
    return;
  }
  return date.toJSON().substr(0, length).replace("T", " ").replace(/-/g, "-");
}

export {
  format
}

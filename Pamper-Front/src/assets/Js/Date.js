export function getCurrentTime() {
  const now = new Date();
  return now.toLocaleString();
}
export function getGreet() {
  const hour = new Date().getHours();
  if (hour >= 5 && hour < 9) {
    return '早上好';
  } else if (hour >= 9 && hour < 11) {
    return '上午好';
  } else if (hour >= 11 && hour < 13) {
    return '中午好';
  } else if (hour >= 13 && hour < 18) {
    return '下午好';
  } else if (hour >= 18 && hour <= 22) {
    return '晚上好';
  } else {
    return '夜深了';
  }
}
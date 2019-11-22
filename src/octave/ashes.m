function d = distribution(angle)
  p = .8;
  b = pi/256;
  d = ;
  if(0 <= angle && angle <= pi/4)
    d = power(b,angle);
    return;
  elseif( pi*7/4 <= angle && angle <= pi*2 )
    d = power(b,-(angle-2*pi));
    return;
  endif

 endfunction

 function dd = dist(angles)
  dd = [];
  for e = angles
    dd = [dd distribution(e)];
  endfor
 endfunction

 x = [0:pi/32:2*pi];

plot(x,dist(x))
hold ("on");
plot(x,dist(x))
set (gca, 'XTick',     0: pi / 2: 2 * pi)
set (gca, 'XTickLabel',{'0', 'pi / 2', 'pi', '3 pi / 2','2 pi'})
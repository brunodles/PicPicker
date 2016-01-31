# PicPicker

[![Release](https://jitpack.io/v/brunodles/PicPicker.svg)](https://jitpack.io/#brunodles/PicPicker)

A simple library to pick pictures from the gallery and camera.
By using a single object to make the requests.

## How add it
Add it to your build.gradle with:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
and:

```gradle
dependencies {
    compile 'com.github.brunodles:PicPicker:{latest version}'
}
```

Ok, now you have the lib on your project, let's see how to use it.

## Seting things up
First make a property on your `activity` or `fragment`, like this.
```private PicPicker picPicker;```

Then on the `onCreate` method you should initialize it.
```picPicker = new PicPicker(imageView, this)```

That `this` on the code means the `ActivityStarter` it's a class that will start the camera or
gallery app intent.
To make it work like that we need to add  `implements ActivityStarter` on our `activity` or
`fragment`. Don't even need to change anything. This is needed to let the lib know where the
response will be sent.

Now we need to pass the result to the lib and to do that we just need to _override_
`onActivityResult` and pass it's parameters to the lib, just like that.
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    picPicker.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}
```

You can even make a validation to check if the lib had used those parameters, it returns `true`
when made something to it.

## Using it
Now to use the lib is so simple, to pick a image from the gallery just call.
```picPicker.gallery();```

If you want to grab a image from the camera call.
```picPicker.camera();```

# Sample
You can see more thing on sample, it have some explanations too.
On the sample you will see how to:
* work with runtime permissions.
* grab the bitmap.
* listen for possible errors

# You can help this lib to grow
If you fond something wrong or if you want some feature, just create a issue or even better create
a pull request with you idea.